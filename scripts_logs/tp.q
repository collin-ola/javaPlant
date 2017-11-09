///////USAGE///////
/q tp.q -log 1 to show logging on console
/q tp.q -log 0 to disable this (still saves to file)
/add -e 1 & a breakpoint in script for debugging 
///////USAGE///////

system"l init.q" /initialisation - logging, ports and message handlers.
system"l security.q" /loads security functions & data for remote authentication.
system"l schemas.q" /table schema(s)
system"c 2000 2000"

.u.toString:{$[type[x] in -10 10h; x; string[x]]}
.u.transLogHandle:hopen`$":transactionLog_",string[.z.D],".log"

.u.upd:{[tbl; data] 
	tbl insert data; /update table with data received from FH
	.u.transLogHandle[enlist(`upd; tbl; data)] /update transaction log
	.u.recCount+:1;
	}
	
.u.counts:{show x!count each get each x}


.z.ps:{[query] VERBOSE"Incoming Asynchronous query from handle  ",string[.z.w],". Contents: ",-3!query;  
		[value query 0][query[1];query[2]]; /expected query format:
		}
		
.z.ts:{show .z.P; .u.counts[tables`]}




