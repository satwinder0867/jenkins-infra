def call(){
    withCredentials([file(credentialsId: 'CLUSTER_LOGGING_VARS', variable: 'FILE')]) {
        sh 'cp $FILE $WORKSPACE/deploy/ocp_cluster_logging_vars.yml'
        
        //sh 'yes | cp -r $FILE /root/clusterLoggingVars.yaml'
    
    }
}