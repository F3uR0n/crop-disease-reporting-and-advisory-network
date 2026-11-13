package com.cdran.model.enums;

/** User role; stored in users.role and used as Spring Security authority ROLE_<name>. */
public enum Role {
    FARMER, EXPERT, FIELD_OFFICER, ADMIN
}
