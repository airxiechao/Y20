package com.airxiechao.y20.pipeline.pojo.constant;

public class EnumPipelineEventType {

    private static final String EVENT_TYPE_PREFIX = "y20_";

    public static final String PIPELINE_RUN_STATUS_UPDATE = EVENT_TYPE_PREFIX + "pipeline_run_status_update";

    public static final String PIPELINE_STEP_RUN_OUTPUT = EVENT_TYPE_PREFIX + "pipeline_step_run_output";
    public static final String PIPELINE_STEP_RUN_STATUS_UPDATE = EVENT_TYPE_PREFIX + "pipeline_step_run_status_update";

    public static final String PIPELINE_EXPLORER_RUN_STATUS_UPDATE = EVENT_TYPE_PREFIX + "pipeline_run_explorer_status_update";
    public static final String PIPELINE_EXPLORER_RUN_TRANSFER_PROGRESS = EVENT_TYPE_PREFIX + "pipeline_run_explorer_transfer_progress";

    public static final String PIPELINE_TERMINAL_RUN_OUTPUT = EVENT_TYPE_PREFIX + "pipeline_run_terminal_output";
    public static final String PIPELINE_TERMINAL_RUN_STATUS_UPDATE = EVENT_TYPE_PREFIX + "pipeline_run_terminal_status_update";

    public static final String STEP_RUN_OUTPUT_PUSH = EVENT_TYPE_PREFIX + "step_run_output_push";
    public static final String TERMINAL_RUN_OUTPUT_PUSH = EVENT_TYPE_PREFIX + "terminal_run_output_push";
    public static final String EXPLORER_RUN_TRANSFER_PROGRESS = EVENT_TYPE_PREFIX + "explorer_run_transfer_progress";

    public static final String PIPELINE_CRON_UPDATE = EVENT_TYPE_PREFIX + "pipeline_cron_update";

    public static final String PIPELINE_RUN_ACTIVITY = EVENT_TYPE_PREFIX + "pipeline_run_activity";

}
