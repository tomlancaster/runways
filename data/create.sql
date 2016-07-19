# countries, airports, runways import

# --- !Ups
.import conf/resources/random-repo/resources/countries.csv countries
.import conf/resources/random-repo/resources/airports.csv airports
.import conf/resources/random-repo/resources/runways.csv runways

create index countries_idx on countries(code);
create index airports_idx on airports(ident);
create index airports_country_idx on airports(iso_country);
create index runways_idx on runways(airport_ident);


# --- !Downs

drop table countries;
drop table airports;
drop table runways;
