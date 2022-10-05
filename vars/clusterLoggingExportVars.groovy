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
                ssh -o 'StrictHostKeyChecking no' -i ${WORKSPACE}/deploy/id_rsa root@${OCP_IP} "chmod 755 /root/update-logging-var.sh; ./root/update-logging-var.sh"  
                scp -o 'StrictHostKeyChecking no' -i ${WORKSPACE}/deploy/id_rsa root@${OCP_IP}:/root/cluster_logging_output.txt ${WORKSPACE}/
            '''
        }
        catch (err) {
            echo 'Error ! Exporting of environment variables failed!'
            env.FAILED_STAGE=env.STAGE_NAME
            throw err
        }
    }
}

