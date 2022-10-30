package org.chromium.content.app;

import android.content.Intent;

public class ChromiumLinkerParams {
    private static final String EXTRA_LINKER_PARAMS_BASE_LOAD_ADDRESS = "org.chromium.content.common.linker_params.base_load_address";
    private static final String EXTRA_LINKER_PARAMS_TEST_RUNNER_CLASS_NAME = "org.chromium.content.common.linker_params.test_runner_class_name";
    private static final String EXTRA_LINKER_PARAMS_WAIT_FOR_SHARED_RELRO = "org.chromium.content.common.linker_params.wait_for_shared_relro";
    public final long mBaseLoadAddress;
    public final String mTestRunnerClassName;
    public final boolean mWaitForSharedRelro;

    public ChromiumLinkerParams(long baseLoadAddress, boolean waitForSharedRelro, String testRunnerClassName) {
        this.mBaseLoadAddress = baseLoadAddress;
        this.mWaitForSharedRelro = waitForSharedRelro;
        this.mTestRunnerClassName = testRunnerClassName;
    }

    public ChromiumLinkerParams(Intent intent) {
        this.mBaseLoadAddress = intent.getLongExtra(EXTRA_LINKER_PARAMS_BASE_LOAD_ADDRESS, 0);
        this.mWaitForSharedRelro = intent.getBooleanExtra(EXTRA_LINKER_PARAMS_WAIT_FOR_SHARED_RELRO, false);
        this.mTestRunnerClassName = intent.getStringExtra(EXTRA_LINKER_PARAMS_TEST_RUNNER_CLASS_NAME);
    }

    public void addIntentExtras(Intent intent) {
        intent.putExtra(EXTRA_LINKER_PARAMS_BASE_LOAD_ADDRESS, this.mBaseLoadAddress);
        intent.putExtra(EXTRA_LINKER_PARAMS_WAIT_FOR_SHARED_RELRO, this.mWaitForSharedRelro);
        intent.putExtra(EXTRA_LINKER_PARAMS_TEST_RUNNER_CLASS_NAME, this.mTestRunnerClassName);
    }

    public String toString() {
        String str = "LinkerParams(baseLoadAddress:0x%x, waitForSharedRelro:%s, testRunnerClassName:%s";
        Object[] objArr = new Object[3];
        objArr[0] = Long.valueOf(this.mBaseLoadAddress);
        objArr[1] = this.mWaitForSharedRelro ? "true" : "false";
        objArr[2] = this.mTestRunnerClassName;
        return String.format(str, objArr);
    }
}
