package com.petivo.api.dto;

public class AdminStatsDto {
    private long totalAppointments;
    private long confirmedAppointments;
    private long pendingAppointments;
    private long totalUsers;
    private long totalPets;
    private long totalServices;

    public AdminStatsDto() {
    }

    public AdminStatsDto(long totalAppointments, long confirmedAppointments, long pendingAppointments, long totalUsers, long totalPets, long totalServices) {
        this.totalAppointments = totalAppointments;
        this.confirmedAppointments = confirmedAppointments;
        this.pendingAppointments = pendingAppointments;
        this.totalUsers = totalUsers;
        this.totalPets = totalPets;
        this.totalServices = totalServices;
    }

    public long getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(long totalAppointments) {
        this.totalAppointments = totalAppointments;
    }

    public long getConfirmedAppointments() {
        return confirmedAppointments;
    }

    public void setConfirmedAppointments(long confirmedAppointments) {
        this.confirmedAppointments = confirmedAppointments;
    }

    public long getPendingAppointments() {
        return pendingAppointments;
    }

    public void setPendingAppointments(long pendingAppointments) {
        this.pendingAppointments = pendingAppointments;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalPets() {
        return totalPets;
    }

    public void setTotalPets(long totalPets) {
        this.totalPets = totalPets;
    }

    public long getTotalServices() {
        return totalServices;
    }

    public void setTotalServices(long totalServices) {
        this.totalServices = totalServices;
    }
}
