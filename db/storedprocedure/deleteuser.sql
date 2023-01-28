CREATE OR REPLACE PROCEDURE public.deleteuser(userid uuid, INOUT resultdata integer)
 LANGUAGE plpgsql
AS $procedure$
	begin
		
	delete from usercashstats where user_id = userid;
	
	delete from suspicious_users where user_id = userid;
	
  select 1 into resultData;
 
	END;
$procedure$
;
