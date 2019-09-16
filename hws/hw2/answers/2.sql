select name, char_length(name) as namelen from games where not minplayers >= 5 and not maxplayers <= 3 order by char_length(name) desc limit 10;
