select c_id, count(g_id) as cnt from gamecat group by c_id having count(g_id) >= 15;
