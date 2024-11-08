package router.publish;

public enum EventType {
    BreakPointStart,
    BreakPointEnd,
    MiddlewareContextCount,
    MiddlewareContextAnalystsComplete,
    FilterCount,
    FilterAnalystsComplete,
    ServletCount,
    ServletAnalystsComplete,
    Jersey1FrameworkCount,
    Jersey1FrameworkAnalystsComplete,
    Jersey2FrameworkCount,
    Jersey2FrameworkAnalystsComplete,
    SpringFrameworkCount,
    SpringFrameworkAnalystsComplete,
    Struts1FrameworkCount,
    Struts1FrameworkAnalystsComplete,
    Struts2FrameworkCount,
    Struts2FrameworkAnalystsComplete,
}
