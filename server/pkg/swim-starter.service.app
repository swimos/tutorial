[Unit]
Description=Swim tutorial
Wants=network.target

[Service]
EnvironmentFile=-/etc/sysconfig/swim-tutorial
ExecStart=/opt/swim-tutorial/bin/swim-tutorial
User=swim-tutorial
Restart=on-failure
LimitNOFILE=65535

[Install]
WantedBy=multi-user.target
