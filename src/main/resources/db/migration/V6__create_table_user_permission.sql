CREATE TABLE tb_user_permission (
  id_user UUID NOT NULL,
  id_permission SMALLINT NOT NULL,
  PRIMARY KEY (id_user, id_permission),
  CONSTRAINT fk_pe_user FOREIGN KEY (id_user) REFERENCES tb_user(id),
  CONSTRAINT fk_us_permission FOREIGN KEY (id_permission) REFERENCES tb_permission(id)
);