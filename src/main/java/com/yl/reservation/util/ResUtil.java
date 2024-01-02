package com.yl.reservation.util;

import com.yl.reservation.exception.GraphQLException;
import com.yl.reservation.exception.HostException;
import com.yl.reservation.model.ContactMethod;
import com.yl.reservation.model.Host;
import com.yl.reservation.model.User;
import com.yl.reservation.service.HostUpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;

public class ResUtil {

    private static final Logger logger = LoggerFactory.getLogger(ResUtil.class);

    public static String getCurrentDateTimeString(){
        return LocalDateTime.now().toString();
    }


    public static void validateContactInfo(User user){
        boolean hasEmail = user.getEmail() != null && StringUtils.hasText(user.getEmail().get(0).getValue());
        boolean hasPhone = user.getPhone() != null && StringUtils.hasText(user.getPhone().get(0).getValue());
        boolean hasPrimaryContact;
        if ((hasEmail && hasPhone)) {
            hasPrimaryContact =
                    (user.getPrimaryContactMethod() == ContactMethod.PHONE || user.getPrimaryContactMethod() == ContactMethod.EMAIL);
        } else {
            if (hasPhone) hasPrimaryContact =user.getPrimaryContactMethod() == ContactMethod.PHONE;
            else hasPrimaryContact = hasEmail && user.getPrimaryContactMethod() == ContactMethod.EMAIL;
        }

        boolean hasContactInfo = (hasEmail || hasPhone) && hasPrimaryContact;
        if (!hasContactInfo)
            throw new HostException(HttpStatus.BAD_REQUEST,"Contact information is missing");
    }



    public static boolean isHostUpdate(Host updatedHost, Host requestHost, boolean isAddressUpdate){
        if (requestHost.getBeds() != updatedHost.getBeds())
            return true;
        if (requestHost.getCrib() != null && !requestHost.getCrib().equals(updatedHost.getCrib()))
            return true;
        if (requestHost.getFullBath() != null && !requestHost.getFullBath().equals(updatedHost.getFullBath()))
            return true;
        if (requestHost.getPrivateEntrance() != null && !requestHost.getPrivateEntrance().equals(updatedHost.getPrivateEntrance()))
            return true;
        if (requestHost.getNotes() != null && !requestHost.getNotes().equals(updatedHost.getNotes()))
            return true;
        return isAddressUpdate && requestHost.getAddress() != null && !requestHost.getAddress().equals(updatedHost.getAddress());
    }

    public static boolean isUserUpdate(User userToUpdate, User userFromRequest){
        //todo: override equals method
//        if (!userFromRequest.equals(userToUpdate))
//            return true;
        if (userFromRequest.getEmail() != null && !userFromRequest.getEmail().equals(userToUpdate.getEmail()))
            return true;
        if (userFromRequest.getPhone() != null && !userFromRequest.getPhone().equals(userToUpdate.getPhone()))
            return true;
        return userFromRequest.getPrimaryContactMethod() != userToUpdate.getPrimaryContactMethod();
    }

    public static User updateUser(User userToUpdate, User userFromRequest, String updateDateTime){
        boolean isUserUpdate = isUserUpdate(userToUpdate, userFromRequest);
        //todo: don't throw exception here. will be a problem when we update host and user...
        if (!isUserUpdate) throw new GraphQLException("No updates to apply to user", HttpStatus.BAD_REQUEST);
        if (userFromRequest.getEmail() != null) userToUpdate.setEmail(userFromRequest.getEmail());
        if (userFromRequest.getPhone() != null) userToUpdate.setPhone(userFromRequest.getPhone());
        if (userFromRequest.getPrimaryContactMethod() != null) userToUpdate.setPrimaryContactMethod(userFromRequest.getPrimaryContactMethod());
        userToUpdate.setLastUpdated(updateDateTime);
        return userToUpdate;

    }

    public static Host updateHost(Host hostToUpdate, Host hostFromRequest, boolean isAddressUpdate, String updateDateTime){
        if (!isHostUpdate(hostToUpdate, hostFromRequest, isAddressUpdate)) {
            //todo: don't throw exception here. will be a problem when we update host and user...
            throw new HostException(HttpStatus.OK, "No updates to apply");
//            logger.info("No host updates to apply.");
//            return null;
        } else {
            if (hostFromRequest.getAddress() != null) hostToUpdate.setAddress(hostFromRequest.getAddress());
            if (hostFromRequest.getBeds() > 0) hostToUpdate.setBeds(hostFromRequest.getBeds());
            if (hostFromRequest.getNotes() != null) hostToUpdate.setNotes(hostFromRequest.getNotes());
            if (hostFromRequest.getCrib() != null) hostToUpdate.setCrib(hostFromRequest.getCrib());
            if (hostFromRequest.getFullBath() != null) hostToUpdate.setFullBath(hostFromRequest.getFullBath());
            if (hostFromRequest.getPrivateEntrance() != null) hostToUpdate.setPrivateEntrance(hostFromRequest.getPrivateEntrance());

            hostToUpdate.setLastUpdated(updateDateTime);

            return hostToUpdate;
        }
    }

    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    public static void validateUserInfo(User user) {
        if (user.getLastName() == null)
            throw new HostException(HttpStatus.BAD_REQUEST, "Last name is missing from the request");
        if (user.getFirstName() == null || !StringUtils.hasText(user.getFirstName()))
            throw new HostException(HttpStatus.BAD_REQUEST,"First name is missing from the request");
        validateContactInfo(user);
    }

    public static void validateHostInfo(Host host) {
        //todo: there may be a scenario where we will not need userId (if we are creating a user...)
        if (host.getUserId() == null)
            throw new GraphQLException("userId is missing from the request", HttpStatus.BAD_REQUEST);
        if (host.getAddress() == null)
            throw new GraphQLException("Address is missing from the request", HttpStatus.BAD_REQUEST);
    }
}
