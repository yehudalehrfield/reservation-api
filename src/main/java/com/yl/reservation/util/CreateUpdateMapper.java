package com.yl.reservation.util;

import com.yl.reservation.exception.ResGraphException;
import com.yl.reservation.exception.ResException;
import com.yl.reservation.model.Guest;
import com.yl.reservation.model.Host;
import com.yl.reservation.model.Reservation;
import com.yl.reservation.model.User;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

public class CreateUpdateMapper {

    // ╔══════╗
    // ║ USER ║
    // ╚══════╝

    public static User updateUser(User userToUpdate, User userFromRequest, String updateDateTime) {
        boolean isUserUpdate = isUserUpdate(userToUpdate, userFromRequest);
        if (!isUserUpdate)
            throw new ResGraphException("No updates to apply to user", HttpStatus.BAD_REQUEST);
        if (userFromRequest.getEmail() != null)
            userToUpdate.setEmail(userFromRequest.getEmail());
        if (userFromRequest.getPhone() != null)
            userToUpdate.setPhone(userFromRequest.getPhone());
        if (userFromRequest.getPrimaryContactMethod() != null)
            userToUpdate.setPrimaryContactMethod(userFromRequest.getPrimaryContactMethod());
        userToUpdate.setLastUpdated(updateDateTime);
        return userToUpdate;
    }

    public static boolean isUserUpdate(User userToUpdate, User userFromRequest) {
        if (userFromRequest.getEmail() != null && !userFromRequest.getEmail().equals(userToUpdate.getEmail()))
            return true;
        if (userFromRequest.getPhone() != null && !userFromRequest.getPhone().equals(userToUpdate.getPhone()))
            return true;
        return userFromRequest.getPrimaryContactMethod() != userToUpdate.getPrimaryContactMethod();
    }

    // ╔═══════╗
    // ║ GUEST ║
    // ╚═══════╝

    public static Guest updateGuest(Guest guestToUpdate, Guest guestFromRequest, String updateDateTime) {
        if (!isGuestUpdate(guestToUpdate, guestFromRequest)) {
            throw new ResException(ResConstants.NO_UPDATES_APPLICABLE, HttpStatus.OK);
        } else {
            if (guestFromRequest.getNickName() != null)
                guestToUpdate.setNickName(guestFromRequest.getNickName());
            if (guestFromRequest.getNumAdults() > 0)
                guestToUpdate.setNumAdults(guestFromRequest.getNumAdults());
            if (guestFromRequest.getNumChildren() > 0)
                guestToUpdate.setNumAdults(guestFromRequest.getNumChildren());
            if (guestFromRequest.getCrib() != null)
                guestToUpdate.setCrib(guestFromRequest.getCrib());
            if (guestFromRequest.getNotes() != null)
                guestToUpdate.setNotes(guestFromRequest.getNotes());

            guestToUpdate.setLastUpdated(updateDateTime);

            return guestToUpdate;
        }
    }

    public static boolean isGuestUpdate(Guest updatedGuest, Guest requestGuest) {
        if (!requestGuest.getNickName().equals(updatedGuest.getNickName()))
            return true;
        if (requestGuest.getNumAdults() != updatedGuest.getNumAdults())
            return true;
        if (requestGuest.getNumChildren() != updatedGuest.getNumChildren())
            return true;
        if (requestGuest.getCrib() != null && !requestGuest.getCrib().equals(updatedGuest.getCrib()))
            return true;
        return !requestGuest.getNotes().equals(updatedGuest.getNotes());
    }

    // ╔══════╗
    // ║ HOST ║
    // ╚══════╝

    public static Host updateHost(Host hostToUpdate, Host hostFromRequest, String updateDateTime) {
        if (!isHostUpdate(hostToUpdate, hostFromRequest)) {
            throw new ResException(ResConstants.NO_UPDATES_APPLICABLE, HttpStatus.OK);
        } else {
            if (hostFromRequest.getAddress() != null)
                hostToUpdate.setAddress(hostFromRequest.getAddress());
            if (hostFromRequest.getBeds() > 0)
                hostToUpdate.setBeds(hostFromRequest.getBeds());
            if (hostFromRequest.getNotes() != null)
                hostToUpdate.setNotes(hostFromRequest.getNotes());
            if (hostFromRequest.getCrib() != null)
                hostToUpdate.setCrib(hostFromRequest.getCrib());
            if (hostFromRequest.getFullBath() != null)
                hostToUpdate.setFullBath(hostFromRequest.getFullBath());
            if (hostFromRequest.getPrivateEntrance() != null)
                hostToUpdate.setPrivateEntrance(hostFromRequest.getPrivateEntrance());

            hostToUpdate.setLastUpdated(updateDateTime);

            return hostToUpdate;
        }
    }

    public static boolean isHostUpdate(Host updatedHost, Host requestHost) {
        if (requestHost.getBeds() != updatedHost.getBeds())
            return true;
        if (requestHost.getCrib() != null && !requestHost.getCrib().equals(updatedHost.getCrib()))
            return true;
        if (requestHost.getFullBath() != null && !requestHost.getFullBath().equals(updatedHost.getFullBath()))
            return true;
        if (requestHost.getPrivateEntrance() != null
                && !requestHost.getPrivateEntrance().equals(updatedHost.getPrivateEntrance()))
            return true;
        return (requestHost.getNotes() != null && !requestHost.getNotes().equals(updatedHost.getNotes()));
    }

    // ╔═════════════╗
    // ║ RESERVATION ║
    // ╚═════════════╝

    public static Reservation updateReservation(Reservation reservationToUpdate, Reservation reservationFromRequest,
            String updateDateTime) {
        if (!isReservationUpdate(reservationToUpdate, reservationFromRequest)) {
            // todo: ResGraphException?
            throw new ResException(ResConstants.NO_UPDATES_APPLICABLE, HttpStatus.OK);
        } else {
            // checking if there are conflicts with other reservations

            if (StringUtils.hasText(reservationFromRequest.getStartDate()))
                reservationToUpdate.setStartDate(reservationFromRequest.getStartDate());
            if (StringUtils.hasText(reservationFromRequest.getEndDate())) {
                LocalDate endDate = LocalDate.parse(reservationFromRequest.getEndDate());
                LocalDate startDate = LocalDate.parse(reservationToUpdate.getStartDate());
                if (endDate.isBefore(startDate) || endDate.isEqual(startDate))
                    throw new ResException("Reservation end date cannot be before reservation start date",
                            HttpStatus.BAD_REQUEST);
                else
                    reservationToUpdate.setEndDate(reservationFromRequest.getEndDate());
            }
            if (StringUtils.hasText(reservationFromRequest.getNotes()))
                reservationToUpdate.setNotes(reservationFromRequest.getNotes());

            reservationToUpdate.setLastUpdated(updateDateTime);

            return reservationToUpdate;
        }
    }

    public static boolean isReservationUpdate(Reservation updatedReservation, Reservation requestReservation) {
        if (!requestReservation.getStartDate().equals(updatedReservation.getStartDate()))
            return true;
        if (!requestReservation.getEndDate().equals(updatedReservation.getEndDate()))
            return true;
        if (!requestReservation.getNotes().equals(updatedReservation.getNotes()))
            return true;
        return false;
    }

}
