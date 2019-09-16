select (min(minplaytime) * 1.0) / 60 as minhrs, (max(maxplaytime) * 1.0) / 60 as maxplay, minplayers, maxplayers from games group by minplayers, maxplayers order by minplayers asc, maxplayers asc;
