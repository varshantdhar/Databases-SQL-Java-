\COPY potholes (creation_date, service_request_number, most_recent_action, potholes_filled, street_address, zip, latitude, longitude) FROM '311_Service_Requests_-_Pot_Holes_Reported.tsv' DELIMITER E'\t' CSV HEADER;