## Context
用户想了解 `agentscope-core` 中 plan execute 的运行流程，重点是 `PlanNotebook` 如何接入 `ReActAgent` 的 reasoning/acting 循环，以及 plan/subtask 的状态如何推进、持久化和收尾。

## Recommended approach
1. 从入口看集成方式：`ReActAgent.Builder.enablePlan()` 会创建默认 `PlanNotebook`；也可显式传入 `planNotebook(...)`。[agentscope-core/src/main/java/io/agentscope/core/ReActAgent.java#L1234-L1329]
2. `build()` 时若存在 `planNotebook`，会调用 `configurePlan()`：
    - 把 `PlanNotebook` 注册成工具；
    - 增加一个 `PreReasoningEvent` hook，在每次 reasoning 前注入 `planNotebook.getCurrentHint()` 产生的 `<system-hint>`。[agentscope-core/src/main/java/io/agentscope/core/ReActAgent.java#L1406-L1435] [agentscope-core/src/main/java/io/agentscope/core/ReActAgent.java#L1545-L1582]
3. 主循环入口是 `doCall()` → `executeIteration(0)` → `reasoning()`；reasoning 前会 `notifyPreReasoningEvent(prepareMessages())`，因此 plan hint 会在模型推理前被拼入输入消息。[agentscope-core/src/main/java/io/agentscope/core/ReActAgent.java#L247-L259] [agentscope-core/src/main/java/io/agentscope/core/ReActAgent.java#L394-L489]
4. `DefaultPlanToHint.generateHint(...)` 根据当前 plan/subtask 状态生成不同提示：
    - 无 plan：提示创建 plan；
    - 全部 TODO：提示先把第一个 subtask 置为 `in_progress`；
    - 有 `in_progress`：提示继续执行或 `finish_subtask`；
    - 无 `in_progress` 但已有 done：提示推进下一个；
    - 全部完成/放弃：提示 `finish_plan`。[agentscope-core/src/main/java/io/agentscope/core/plan/hint/DefaultPlanToHint.java#L37-L54] [agentscope-core/src/main/java/io/agentscope/core/plan/hint/DefaultPlanToHint.java#L75-L145] [agentscope-core/src/main/java/io/agentscope/core/plan/hint/DefaultPlanToHint.java#L165-L253]
5. `PlanNotebook` 本身提供计划工具：
    - `create_plan` 创建当前 plan；[agentscope-core/src/main/java/io/agentscope/core/plan/PlanNotebook.java#L267-L330]
    - `update_subtask_state` 负责把 subtask 设为 `todo/in_progress/abandoned`，并强制顺序执行、且同一时间只能一个 `in_progress`。[agentscope-core/src/main/java/io/agentscope/core/plan/PlanNotebook.java#L579-L653]
    - `finish_subtask` 会把当前 subtask 标记为 `done`，并自动激活下一个 subtask 为 `in_progress`。[agentscope-core/src/main/java/io/agentscope/core/plan/PlanNotebook.java#L662-L736]
    - `finish_plan` 把 plan 置为 `done/abandoned`，写入历史存储后清空 `currentPlan`。[agentscope-core/src/main/java/io/agentscope/core/plan/PlanNotebook.java#L824-L868]
6. 数据模型：
    - `Plan` 持有 plan 元信息、subtasks、整体状态；[agentscope-core/src/main/java/io/agentscope/core/plan/model/Plan.java#L44-L117]
    - `SubTask` 持有 name/description/expectedOutcome/outcome/state，并支持 `finish()`；[agentscope-core/src/main/java/io/agentscope/core/plan/model/SubTask.java#L43-L156]
    - 状态枚举见 `PlanState` / `SubTaskState`。[agentscope-core/src/main/java/io/agentscope/core/plan/model/PlanState.java#L22-L49] [agentscope-core/src/main/java/io/agentscope/core/plan/model/SubTaskState.java#L22-L49]
7. 状态与历史：
    - 当前活动 plan 通过 `PlanNotebookState` 持久化到 session；`ReActAgent.saveTo/loadFrom` 会按 `StatePersistence` 配置保存/恢复 plan notebook 状态。[agentscope-core/src/main/java/io/agentscope/core/state/PlanNotebookState.java#L15-L43] [agentscope-core/src/main/java/io/agentscope/core/ReActAgent.java#L189-L242]
    - 已结束的 plan 通过 `PlanStorage` 存历史，默认实现是 `InMemoryPlanStorage`。[agentscope-core/src/main/java/io/agentscope/core/plan/storage/PlanStorage.java#L21-L50] [agentscope-core/src/main/java/io/agentscope/core/plan/storage/InMemoryPlanStorage.java#L24-L68]
8. 测试可作为行为佐证：
    - `finishSubtask` 后会自动激活下一步；
    - `finishSubtask` 并不强制当前 task 先显式设为 `in_progress`，只要求前序 task 已完成；这是当前实现的一个细节。[agentscope-core/src/test/java/io/agentscope/core/plan/PlanNotebookToolTest.java#L119-L153]

## Critical files
- [agentscope-core/src/main/java/io/agentscope/core/ReActAgent.java](agentscope-core/src/main/java/io/agentscope/core/ReActAgent.java)
- [agentscope-core/src/main/java/io/agentscope/core/plan/PlanNotebook.java](agentscope-core/src/main/java/io/agentscope/core/plan/PlanNotebook.java)
- [agentscope-core/src/main/java/io/agentscope/core/plan/hint/DefaultPlanToHint.java](agentscope-core/src/main/java/io/agentscope/core/plan/hint/DefaultPlanToHint.java)
- [agentscope-core/src/main/java/io/agentscope/core/plan/model/Plan.java](agentscope-core/src/main/java/io/agentscope/core/plan/model/Plan.java)
- [agentscope-core/src/main/java/io/agentscope/core/plan/model/SubTask.java](agentscope-core/src/main/java/io/agentscope/core/plan/model/SubTask.java)
- [agentscope-core/src/main/java/io/agentscope/core/state/PlanNotebookState.java](agentscope-core/src/main/java/io/agentscope/core/state/PlanNotebookState.java)

## Verification
- 阅读 `ReActAgent.configurePlan()`、`reasoning()`、`PlanNotebook.getCurrentHint()`，确认 hint 注入点。
- 阅读 `PlanNotebook` 的 `create_plan` / `update_subtask_state` / `finish_subtask` / `finish_plan`，确认状态流转。
- 参考 `PlanNotebookToolTest` 验证自动推进和边界行为。
