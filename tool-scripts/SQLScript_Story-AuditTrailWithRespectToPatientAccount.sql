#Story-14583 : Create audit services report
#This query will show id, account status (enabled or disabled), created date, last updated date, last updated by and created by

use ums;

select 
	q2.id,
    q2.disabled,
    q2.created_date,
    q2.last_updated_date,
    q2.last_updated_by,
    q2.verified,
    CONCAT(users.givenname, " ", users.familyname) as created_by
from    
	(select 
		q_first_pass.id,
		q_first_pass.disabled,
        q_first_pass.created_by,
		q_first_pass.created_date,
		q_first_pass.last_updated_date,
		q_first_pass.last_updated_by,
		user_activation.verified
	from    
	(select 
		user.id,
		user.disabled,
		user.created_date,
        user.created_by,
		user.last_updated_date,
		CONCAT(users.givenname, " ", users.familyname) as last_updated_by
	from
		user, uaa.users, user_roles
	where
		user.last_updated_by = users.id
	and
		user.id = user_roles.users_id
	and
		user_roles.roles_id = 1) as q_first_pass	
	left join
		user_activation
	on
		q_first_pass.id = user_activation.user_id) as q2 	
left join
	uaa.users
on
	q2.created_by = users.id;
    
    

 
    