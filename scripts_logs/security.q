/function documentation
/.sec.tblErr: error message
/.sec.chars: string of characters, for creating unique salts
/.sec.createTbl: creates blank table
/.sec.toString: converts input to a string
/.encryptPW: salts and encrypts user's password
/.sec.addUser: used to add a record to the users table, before persisting.


.sec.err:{-1"User table not found. Please create a user using .sec.addUser[`un;\"pw\"]";}
.sec.chars:.Q.nA,.Q.a
.sec.createTbl:{([username:`$()] salt:(); password:())}
.sec.toString:{$[type[x] in -10 10h; x; string x]}
.sec.encryptPW:{[pw;salt] md5 salt,.sec.toString[pw]}
.sec.addUser:{[un;pw] salt:14?.sec.chars;
					`.sec.userTbl upsert (un; salt; .sec.encryptPW[pw;salt]);
					`:userTbl set .sec.userTbl;
					INFO"New user ", string[un], " has been added."}
					
/Authenticates provided login details.
.z.pw:{[user;pass] $[.sec.encryptPW[pass; .sec.userTbl[user][`salt]]~.sec.userTbl[user][`password]; 
		[INFO"Successful login by user ", string[user],"."; 1b]; 
			[INFO"Login attempt failed. Credentials provided: ", string[user],":",pass ;0b]]}
					
/errors trapping, for if no user logins exist.
.sec.userTbl:@[get; `:userTbl; {.sec.err[]; .sec.createTbl[]}];

