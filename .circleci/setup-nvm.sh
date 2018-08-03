#!/usr/bin/env bash -e

cd /tmp
wget -q https://raw.githubusercontent.com/creationix/nvm/v0.33.11/install.sh
bash /tmp/install.sh
cat << EOF >> ~/.bash_profile
export NVM_DIR="$HOME/.nvm"
[ -s "\$NVM_DIR/nvm.sh" ] && source "\$NVM_DIR/nvm.sh" # This loads nvm
EOF
