FROM postgres:16

RUN apt-get update \
  && apt-get install -y postgresql-16-cron \
  && echo "shared_preload_libraries = 'pg_cron'" >> /usr/share/postgresql/postgresql.conf.sample \
  && echo "cron.database_name = 'sigms'" >> /usr/share/postgresql/postgresql.conf.sample