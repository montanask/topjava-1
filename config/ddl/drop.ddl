
    alter table if exists meals 
       drop constraint if exists FK677c66qpjr7234luomahc1ale

    alter table if exists user_roles 
       drop constraint if exists FKhfh9dx7w3ubf1co1vdev94g3f

    drop table if exists meals cascade

    drop table if exists user_roles cascade

    drop table if exists users cascade

    drop sequence if exists global_seq
