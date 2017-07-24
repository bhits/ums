#Story-14582: Look at audit trail so I can understand who has changed my profile

use ums;

#Query to retrive who changed the profile and when
#Replace ? with user id
select
	q1.id,
    q1.last_updated_date,
    q1.created_date,
    q1.last_updated_by_name,
    CONCAT(users.givenname, " ", users.familyname) as created_by_name
from 
	(select
		user.id,
		last_updated_date,
        created_date,
        created_by,
		CONCAT(users.givenname, " ", users.familyname) as last_updated_by_name
	from 
		user
	left join
		uaa.users
	on 
		user.last_updated_by = users.id    
	where 
		user.id = '?') as q1
left join
	uaa.users
on
	q1.created_by = users.id;

#Query to pull up profile updates
#Replace ? with user id
select
	q1.id,
    #q1.last_updated_by,
    q1.last_updated_date,
    #q1.created_by,
    q1.created_date,
    q1.last_updated_by_name,
    CONCAT(users.givenname, " ", users.familyname) as created_by_name
from    
	(select 
		user_aud.id,
		user_aud.last_updated_by,
		user_aud.last_updated_date,
		user_aud.created_by,
		user_aud.created_date,
		CONCAT(users.givenname, " ", users.familyname) as last_updated_by_name
	from 
		user_aud
	left join
		uaa.users
	on
		user_aud.last_updated_by = users.id 
	where 
		user_aud.id = '?') as q1
left join
	uaa.users
on
	q1.created_by = users.id
order by
	q1.last_updated_date	

