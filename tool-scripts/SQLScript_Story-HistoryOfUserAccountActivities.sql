#Story 14615:As a developer I will have history of user account activities (login, changes to profile)
#These queries will pull up account activities related to profile updates

use ums;

#Pull up account activities related to profile for a selected staff account
#Use username of the staff in place of ?
select 
	x.id,
	x.last_updated_by,
	x.last_updated_date,
	x.created_by,
	x.created_date,
	CONCAT(y.givenname, " ", y.familyname) as patient_name 
from 
	user_aud x
inner join
	uaa.users y
on 
	x.last_updated_by = y.id
where
	y.username = '?';

#Pull up account activities related to profile for a selected provider account
#User username of the provider in place of ?
select 
	x.id,
	x.last_updated_by,
	x.last_updated_date,
	x.created_by,
	x.created_date,
	CONCAT(y.givenname, " ", y.familyname) as patient_name
from 
	user_aud x
inner join
	uaa.users y
on 
	x.last_updated_by = y.id
where
	y.username = '?';


#Pull up account activities related to profile for a selected patient
#Use username of the provider in place of ?
select 
	x.id,
	x.last_updated_by,
	x.last_updated_date,
	x.created_by,
	x.created_date,
	CONCAT(y.givenname, " ", y.familyname) as patient_name
from 
	user_aud x
left join
	uaa.users y
on 
	x.last_updated_by = y.id
where
	y.username = '?';

