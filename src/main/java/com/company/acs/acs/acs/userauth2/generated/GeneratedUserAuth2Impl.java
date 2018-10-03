package com.company.acs.acs.acs.userauth2.generated;

import com.company.acs.acs.acs.user.User;
import com.company.acs.acs.acs.userauth2.UserAuth2;
import com.speedment.common.annotation.GeneratedCode;
import com.speedment.runtime.core.manager.Manager;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * The generated base implementation of the {@link
 * com.company.acs.acs.acs.userauth2.UserAuth2}-interface.
 * <p>
 * This file has been automatically generated by Speedment. Any changes made to
 * it will be overwritten.
 * 
 * @author Speedment
 */
@GeneratedCode("Speedment")
public abstract class GeneratedUserAuth2Impl implements UserAuth2 {
    
    private int authId;
    private int authUserId;
    private String hash;
    private String salt;
    
    protected GeneratedUserAuth2Impl() {}
    
    @Override
    public int getAuthId() {
        return authId;
    }
    
    @Override
    public int getAuthUserId() {
        return authUserId;
    }
    
    @Override
    public String getHash() {
        return hash;
    }
    
    @Override
    public String getSalt() {
        return salt;
    }
    
    @Override
    public UserAuth2 setAuthId(int authId) {
        this.authId = authId;
        return this;
    }
    
    @Override
    public UserAuth2 setAuthUserId(int authUserId) {
        this.authUserId = authUserId;
        return this;
    }
    
    @Override
    public UserAuth2 setHash(String hash) {
        this.hash = hash;
        return this;
    }
    
    @Override
    public UserAuth2 setSalt(String salt) {
        this.salt = salt;
        return this;
    }
    
    @Override
    public User findAuthUserId(Manager<User> foreignManager) {
        return foreignManager.stream().filter(User.USER_ID.equal(getAuthUserId())).findAny().orElse(null);
    }
    
    @Override
    public String toString() {
        final StringJoiner sj = new StringJoiner(", ", "{ ", " }");
        sj.add("authId = "     + Objects.toString(getAuthId()));
        sj.add("authUserId = " + Objects.toString(getAuthUserId()));
        sj.add("hash = "       + Objects.toString(getHash()));
        sj.add("salt = "       + Objects.toString(getSalt()));
        return "UserAuth2Impl " + sj.toString();
    }
    
    @Override
    public boolean equals(Object that) {
        if (this == that) { return true; }
        if (!(that instanceof UserAuth2)) { return false; }
        final UserAuth2 thatUserAuth2 = (UserAuth2)that;
        if (this.getAuthId() != thatUserAuth2.getAuthId()) { return false; }
        if (this.getAuthUserId() != thatUserAuth2.getAuthUserId()) { return false; }
        if (!Objects.equals(this.getHash(), thatUserAuth2.getHash())) { return false; }
        if (!Objects.equals(this.getSalt(), thatUserAuth2.getSalt())) { return false; }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Integer.hashCode(getAuthId());
        hash = 31 * hash + Integer.hashCode(getAuthUserId());
        hash = 31 * hash + Objects.hashCode(getHash());
        hash = 31 * hash + Objects.hashCode(getSalt());
        return hash;
    }
}