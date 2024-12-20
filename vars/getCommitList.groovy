def call() {
    def commitListRaw = sh(script: "${SCRIPT_PATH}/commt_list.sh ${env.GIT_PREVIOUS_SUCCESSFUL_COMMIT}" , returnStdout: true).trim()
    return commitListRaw.split('\n') as List<String>
}