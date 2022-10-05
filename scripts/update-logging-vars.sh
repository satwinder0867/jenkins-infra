#!/bin/bash

chmod 755 logging_vars.sh
source logging_vars.sh

pushd ocp_ansible_validation

BASTION_HOSTNAME=`hostname -s`
echo $BASTION_HOSTNAME
sed -i 's|BASTION_HOSTNAME|'$BASTION_HOSTNAME'|g' cl_inventory

popd

pushd ocp_ansible_validation/examples

sed -i 's|{CHANNEL}|'$CHANNEL'|g' ocp_cluster_logging_vars.yml
sed -i 's|{ELASTICSEARCH_INDEX}|'$ELASTICSEARCH_INDEX'|g' ocp_cluster_logging_vars.yml
sed -i 's|{CLUSTERLOGGING_INDEX}|'$CLUSTERLOGGING_INDEX'|g' ocp_cluster_logging_vars.yml
sed -i 's|{CLUSTERLOGGING_INDEX}|'$CLUSTERLOGGING_INDEX'|g' ocp_cluster_logging_vars.yml
sed -i 's|{LOGCOLLECTORTYPE}|'$LOGCOLLECTORTYPE'|g' ocp_cluster_logging_vars.yml
sed -i 's|{CLFCLEANUP}|'${env.CLFCLEANUP}'|g' ocp_cluster_logging_vars.yml
sed -i 's|{OCPVERSION}|'$OCPVERSION'|g' ocp_cluster_logging_vars.yml

pushd ../
ansible-playbook -i cl_inventory -e @examples/ocp_cluster_logging_vars.yml playbooks/main.yml | tee /root/cluster_logging_output.txt
