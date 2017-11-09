/log file handle. creates a new file if one has not been created for today.
sysLog:`$":sysLog_",string[.z.D],".log"
sysLogHandle:hopen sysLog

/saves log to file. command line argument determines if message is displayed on screen.
lg:{[level; msg] toSave:string[.z.P]," [", string[level] ,"] ", $[type[msg] in -10 10h; msg; -3!msg];
	sysLogHandle[toSave,"\n"];
	if[(first "J"$.Q.opt[.z.x][`log])~1; -1 toSave];}

/create projections for different logging levels
logLevels:`DEBUG`VERBOSE`INFO`WARN`FATAL;
{[level] level set lg[level]} each logLevels;