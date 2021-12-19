package com.shanjupay.uaa.repository;

import com.shanjupay.uaa.domain.OauthClientDetails;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OauthClientDetailsRowMapper implements RowMapper<OauthClientDetails> {
    public OauthClientDetailsRowMapper() {}

    @Override
    public OauthClientDetails mapRow(ResultSet resultSet, int i) throws SQLException {
        OauthClientDetails clientDetails = new OauthClientDetails();

        clientDetails.clientId(resultSet.getString("client_id"));
        clientDetails.resourceIds(resultSet.getString("resource_ids"));
        clientDetails.clientSecret(resultSet.getString("client_secret"));

        clientDetails.scope(resultSet.getString("scope"));
        clientDetails.authorizedGrantTypes(resultSet.getString("authorized_grant_types"));
        clientDetails.webServerRedirectUri(resultSet.getString("web_server_redirect_uri"));

        clientDetails.authorities(resultSet.getString("authorities"));
        clientDetails.accessTokenValidity(getInteger(resultSet, "access_token_validity"));
        clientDetails.refreshTokenValidity(getInteger(resultSet, "refresh_token_validity"));

        clientDetails.additionalInformation(resultSet.getString("additional_information"));
        clientDetails.createTime(resultSet.getTimestamp("create_time").toLocalDateTime());
        clientDetails.archived(resultSet.getBoolean("archived"));

        clientDetails.trusted(resultSet.getBoolean("trusted"));
        clientDetails.autoApprove(resultSet.getString("autoapprove"));
        return null;
    }

    private Integer getInteger(ResultSet resultSet, String columnName) throws SQLException {
        final Object object = resultSet.getObject(columnName);
        if (object != null) {
            return (Integer) object;
        }
        return null;
    }
}
