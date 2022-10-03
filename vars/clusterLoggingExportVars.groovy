def call(){
    script {
        ansiColor('xterm') {
            echo ""
        }
        try {
            sh '''
                ssh -o 'StrictHostKeyChecking no' -i ${WORKSPACE}/deploy/id_rsa root@${OCP_IP} "rm -rf /root/ocp_ansible_validation; git clone https://satwsin1:${GITHUBTOKEN}@github.ibm.com/redstack-power/ocp_ansible_validation.git"
                scp -o 'StrictHostKeyChecking no' -i ${WORKSPACE}/deploy/id_rsa deploy/ocp_cluster_logging_vars.yml root@${OCP_IP}:/root/ocp_ansible_validation/examples/
                scp -o 'StrictHostKeyChecking no' -i ${WORKSPACE}/deploy/id_rsa logging_vars.sh root@${OCP_IP}:/root/ 
                scp -o 'StrictHostKeyChecking no' -i ${WORKSPACE}/deploy/id_rsa deploy/cl_inventory root@${OCP_IP}:/root/ocp_ansible_validation/

                scp -o 'StrictHostKeyChecking no' -i ${WORKSPACE}/deploy/id_rsa ${WORKSPACE}/scripts/update-logging-var.sh root@${OCP_IP}:/root/           

            '''
            sh (returnStdout: false, script: "/bin/bash ${WORKSPACE}/scripts/update-logging-vars.sh || true")
        }
        catch (err) {
            echo 'Error ! Exporting of environment variables failed!'
            env.FAILED_STAGE=env.STAGE_NAME
            throw err
        }
    }
}

