package router.publish;

public class StartEvent implements Event {
    private EventType eventType;
    private Long uniqId;
    private String taskName;
    private Integer taskCount;

    public StartEvent(EventType eventType, Long uniqId, String taskName, Integer taskCount) {
        this.eventType = eventType;
        this.uniqId = uniqId;
        this.taskName = taskName;
        this.taskCount = taskCount;
    }

    public Long getUniqId() {
        return uniqId;
    }

    public void setUniqId(Long uniqId) {
        this.uniqId = uniqId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Integer getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(Integer taskCount) {
        this.taskCount = taskCount;
    }

    @Override
    public EventType getType() {
        return eventType;
    }
}
