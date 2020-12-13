package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CredentialMapper {

    @Select("SELECT * FROM CREDENTIALS WHERE userid = #{userId}")
    List<Credential> getCredentialsByUserId(Integer userId);

    @Update("UPDATE CREDENTIALS SET credentialid = #{credentialId}, url = #{url}, username = #{username}, key = #{key}, password = #{password} WHERE credentialid = #{credentialId}")
    void updateCredential(Credential credential);

    @Insert("INSERT INTO CREDENTIALS (url, username, key, password, userid) VALUES(#{url}, #{username}, #{key}, #{password}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "credentialId")
    Integer saveCredential(Credential note);

    @Delete("DELETE FROM CREDENTIALS WHERE credentialid=#{credentialId} AND userid=#{userId}")
    Integer deleteCredential(Integer credentialId, Integer userId);


    @Select("SELECT * FROM CREDENTIALS WHERE credentialid = #{credentialId}")
    Credential getCredentialById(Integer credentialId);
}
