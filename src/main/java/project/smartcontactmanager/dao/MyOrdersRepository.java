package project.smartcontactmanager.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import project.smartcontactmanager.entities.MyOrders;

public interface MyOrdersRepository extends JpaRepository<MyOrders, Long> {
    public MyOrders findByOrderId(String orderId);
}
