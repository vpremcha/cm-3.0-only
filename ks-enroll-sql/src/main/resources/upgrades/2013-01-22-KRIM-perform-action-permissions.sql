--Permissions to perform action 'delete' for 'KS Department Schedule Coordinator - Org'
insert into KRIM_PERM_T (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND) values (KRIM_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT perm_tmpl_id FROM krim_perm_tmpl_t where nm = 'Perform Action' and nmspc_cd = 'KS-ENR'), 'KS-ENR', 'Perform Action for Course Offering', 'Allows the user to Perform Action for Course Offering', 'Y')
/
insert into KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) values (KRIM_ATTR_DATA_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT perm_id from krim_perm_t where nm = 'Perform Action for Course Offering' and nmspc_cd = 'KS-ENR'), (SELECT kim_typ_id from krim_typ_t where nm = 'Default' and nmspc_cd = 'KUALI'), (SELECT MIN(TO_NUMBER(kim_attr_defn_id)) from krim_attr_defn_t where nm = 'viewId' and nmspc_cd = 'KR-KRAD'), 'courseOfferingManagementView')
/
insert into KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) values (KRIM_ATTR_DATA_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT perm_id from krim_perm_t where nm = 'Perform Action for Course Offering' and nmspc_cd = 'KS-ENR'), (SELECT kim_typ_id from krim_typ_t where nm = 'Default' and nmspc_cd = 'KUALI'), (SELECT MIN(TO_NUMBER(kim_attr_defn_id)) from krim_attr_defn_t where nm = 'actionEvent' and nmspc_cd = 'KR-KRAD'), 'delete')
/
insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) values (KRIM_ROLE_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT ROLE_ID FROM KRIM_ROLE_T where ROLE_NM = 'KS Department Schedule Coordinator - Org' and nmspc_cd = 'KS-ENR'), (SELECT perm_id from krim_perm_t where nm = 'Perform Action for Course Offering' and nmspc_cd = 'KS-ENR'), 'Y')
/
insert into KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) values (KRIM_ATTR_DATA_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT perm_id from krim_perm_t where nm = 'Perform Action for Course Offering' and nmspc_cd = 'KS-ENR'), (SELECT kim_typ_id from krim_typ_t where nm = 'KS ENR Permission Expression' and nmspc_cd = 'KS-ENR'), (SELECT MIN(TO_NUMBER(kim_attr_defn_id)) from krim_attr_defn_t where nm = 'permissionExpression' and nmspc_cd = 'KS-ENR'), '[''socState'']==''Active'' and [''coState'']==''Draft''')
/
--Permissions to perform action 'delete' for 'Admin' (no conditions)
insert into KRIM_PERM_T (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND) values (KRIM_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT perm_tmpl_id FROM krim_perm_tmpl_t where nm = 'Perform Action' and nmspc_cd = 'KS-ENR'), 'KS-ENR', 'Perform Action for Course Offering for Admin', 'Allows the Admin to Perform Action for Course Offering', 'Y')
/
insert into KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) values (KRIM_ATTR_DATA_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT perm_id from krim_perm_t where nm = 'Perform Action for Course Offering for Admin' and nmspc_cd = 'KS-ENR'), (SELECT kim_typ_id from krim_typ_t where nm = 'Default' and nmspc_cd = 'KUALI'), (SELECT MIN(TO_NUMBER(kim_attr_defn_id)) from krim_attr_defn_t where nm = 'viewId' and nmspc_cd = 'KR-KRAD'), 'courseOfferingManagementView')
/
insert into KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) values (KRIM_ATTR_DATA_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT perm_id from krim_perm_t where nm = 'Perform Action for Course Offering for Admin' and nmspc_cd = 'KS-ENR'), (SELECT kim_typ_id from krim_typ_t where nm = 'Default' and nmspc_cd = 'KUALI'), (SELECT MIN(TO_NUMBER(kim_attr_defn_id)) from krim_attr_defn_t where nm = 'actionEvent' and nmspc_cd = 'KR-KRAD'), 'delete')
/
insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) values (KRIM_ROLE_PERM_ID_S.NEXTVAL, SYS_GUID(), 1, (SELECT ROLE_ID FROM KRIM_ROLE_T where ROLE_NM = 'KS Schedule Coordinator' and nmspc_cd = 'KS-ENR'), (SELECT perm_id from krim_perm_t where nm = 'Perform Action for Course Offering for Admin' and nmspc_cd = 'KS-ENR'), 'Y')
/