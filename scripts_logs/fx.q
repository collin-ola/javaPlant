/Usage
/q fx.q -fn GBPUSD.csv -log 0 (no logs are shown)
/q fx.q -fn GBPUSD.csv -log 1 (shows logs)
system"l log.q";

tpHandle:(neg)hopen hsym `$"::",raze read0[`:tpPort.port],":feed2:feed2pass";

{ //parse csv fx quote data - to be sent to TP.
	path: first hsym `$.Q.opt[.z.x][`fn];
	raw:("*FF"; csv) 0:path;
	dateTime:" "vs/: raw[0];	
	tblFx:: flip `date`time`pair`bid`ask!({"D"$(4#x), "-", (2#-4#x), "-", (-2#x)} each dateTime[;0];
										{"T"$x} each dateTime[;1];
										`GBPUSD;
										raw[1]; 
										raw[2]);
	}[]

	
sendData:{
	toSend: value exec from tblFx where i=x;
	/error trapping
	@[tpHandle;[(".u.upd";`fxQuote;toSend)]; {[err]show "Error: Failed to send tick data. Error type: ", err; exit 1}];
	delete from `tblFx where i=x;
	/system"t ", string[1 + first[1?500]];
	}
	
i:0;

.z.ts:{
	sendData[0];
	i+:1;
	VERBOSE"Sending data packet ", string[i];
	}
	
system"t 1000";
	




		


