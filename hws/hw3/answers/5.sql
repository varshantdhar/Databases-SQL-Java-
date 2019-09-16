select d.user_id, d.user_name, d.business_name from (select c.user_id, c.user_name, c.stars, business.name as business_name from business, (select b.user_id, b.user_name, review.business_id, review.stars as stars from review, (select a.user_id, name as user_name, num_reviews, average_stars from users inner join (select user_id, count(user_id) as num_reviews from review group by user_id having count(user_id) > 50 order by num_reviews desc) as a on users.user_id = a.user_id where average_stars = (select min(average_stars) from users inner join (select user_id, count(user_id) as num_reviews from review group by user_id having count(user_id) > 50 order by num_reviews desc) as a on users.user_id = a.user_id)) as b where b.user_id = review.user_id) as c where c.business_id = business.business_id) as d where stars = (select max(stars) from (select c.user_id, c.user_name, c.stars, business.name as business_name from business, (select b.user_id, b.user_name, review.business_id, review.stars as stars from review, (select a.user_id, name as user_name, num_reviews, average_stars from users inner join (select user_id, count(user_id) as num_reviews from review group by user_id having count(user_id) > 50 order by num_reviews desc) as a on users.user_id = a.user_id where average_stars = (select min(average_stars) from users inner join (select user_id, count(user_id) as num_reviews from review group by user_id having count(user_id) > 50 order by num_reviews desc) as a on users.user_id = a.user_id)) as b where b.user_id = review.user_id) as c where c.business_id = business.business_id) as d);