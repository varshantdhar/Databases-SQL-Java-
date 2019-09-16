select categories.category as category, avg(games.avgscore) as avg from games, gamecat, categories where games.g_id = gamecat.g_id and gamecat.c_id = categories.c_id group by categories.c_id having count(categories.c_id) >= 15 order by avg desc limit 5;
