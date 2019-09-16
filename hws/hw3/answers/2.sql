select avg(a.average_stars) as avg from (select name, count(review.user_id) as review_count, average_stars from review inner join users on review.user_id = users.user_id group by users.user_id order by review_count desc limit 10) as a;