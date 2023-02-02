use finance_tracker;

SELECT * FROM finance_tracker.record;

select * from record;

select * from record where record.timestamp='2022-10-08';

SELECT * from record where sender_id='abc123' or receiver_id='abc123'; 

select substring(record.transaction_ID, 1, 6) as transaction_id, record.trans_date, concat(user.first_name, " ", user.last_name) as sender_name, concat(u2.first_name, " ", u2.last_name) as receiver_name, record.amount, category.category_name from record join user on record.sender_id=user.user_id  join user u2 on record.receiver_id=u2.user_id join category on record.category_id=category.category_id where record.sender_id='vishwas21' or record.receiver_id='vishwas21'
order by trans_date desc;

#filter according to time and category
select substring(record.transaction_ID, 1, 6) as transaction_id, record.trans_date, concat(user.first_name, " ", user.last_name) as sender_name, concat(u2.first_name, " ", u2.last_name) as receiver_name, record.amount, category.category_name from record join user on record.sender_id=user.user_id  join user u2 on record.receiver_id=u2.user_id join category on record.category_id=category.category_id
where (sender_id='vishwas21' or receiver_id='vishwas21')
and record.category_id= 7
and record.trans_date between '2022-07-01' and '2022-10-16'
order by trans_date desc;

#filter according to category
select substring(record.transaction_ID, 1, 6) as transaction_id, record.trans_date, concat(user.first_name, " ", user.last_name) as sender_name, concat(u2.first_name, " ", u2.last_name) as receiver_name, record.amount, category.category_name from record join user on record.sender_id=user.user_id  join user u2 on record.receiver_id=u2.user_id join category on record.category_id=category.category_id
where (sender_id='vishwas21' or receiver_id='vishwas21')
and record.category_id=
(
	select category.category_id
    from category
    where category.category_name='leisure'
) order by trans_date;

#filter accoriding to time
select substring(record.transaction_ID, 1, 6) as transaction_id, record.trans_date, concat(user.first_name, " ", user.last_name) as sender_name, concat(u2.first_name, " ", u2.last_name) as receiver_name, record.amount, category.category_name from record join user on record.sender_id=user.user_id  join user u2 on record.receiver_id=u2.user_id join category on record.category_id=category.category_id
where (sender_id='vishwas21' or receiver_id='vishwas21') 
and record.trans_date between '2022-08-03' and '2022-11-03'
order by trans_date desc;

#create account
insert into user values('123abc','password','alpha', 'beta', '40000', null);

#make transaction - verify receiver
select user_id from user where first_name='vikas';

#number of users
select count(user_id) from user;

#sum of expense
select sum(amount) from record 
where sender_id='vishwas21';
#and category_id = 10;

#sum of income
select sum(amount) from record 
where receiver_id='vishwas21'
and category_id = 10;


