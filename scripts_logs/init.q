system"l log.q"

/logs for when connections are successfully opened and closed.
.z.po:{[handle] INFO".z.po Connection opened by user ", string[.z.u], " on handle ", string[handle], "."}
.z.pc:{[oldhandle] INFO"Connection closed for handle ", string[oldhandle];}

/create & save port to file.
system"p 0W"
port:system"p"
`:tpPort.port 0:enlist string[port];
INFO"Port set to ", string[port], " in file `:tpPort.port";
/TODO: Add directory to the above log message.
