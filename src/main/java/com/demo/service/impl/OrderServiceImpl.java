/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.demo.service.impl;

import com.demo.entity.Address;
import com.demo.entity.Order;
import com.demo.entity.OrderItem;
import com.demo.repository.*;
import com.demo.service.ExampleService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@Primary
public class OrderServiceImpl implements ExampleService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderItemMapper orderItemMapper;

    @Resource
    private AddressMapper addressMapper;

    @Override
    public void initEnvironment() throws SQLException {
        orderMapper.createTableIfNotExists();
        orderItemMapper.createTableIfNotExists();
        orderMapper.truncateTable();
        orderItemMapper.truncateTable();
        initAddressTable();
    }

    private void initAddressTable() throws SQLException {
        addressMapper.createTableIfNotExists();
        addressMapper.truncateTable();
        for (int i = 1; i <= 10; i++) {
            Address entity = new Address();
            entity.setAddressId((long) i);
            entity.setAddressName("address_" + String.valueOf(i));
            addressMapper.insert(entity);
        }
    }

    @Override
    public void cleanEnvironment() throws SQLException {
        orderMapper.dropTable();
        orderItemMapper.dropTable();
    }

    @Override
    @Transactional
    public void processSuccess() throws SQLException {
        System.out.println("-------------- Process Success Begin ---------------");
        List<Long> orderIds = insertData();
        printData();
        deleteData(orderIds);
        printData();
        System.out.println("-------------- Process Success Finish --------------");
    }


    @Transactional(rollbackFor = SQLException.class)
    public void insertTest() throws SQLException {
        System.out.println("-------------- insertTest Begin ---------------");

        Order order = new Order();
        order.setOrderId(1);
        order.setUserId(1);
        order.setAddressId(1);
        order.setStatus("INSERT_TEST");
        orderMapper.insert(order);

        order = new Order();
        order.setOrderId(2);
        order.setUserId(1);
        order.setAddressId(1);
        order.setStatus("INSERT_TEST");
        orderMapper.insert(order);


        order = new Order();
        order.setOrderId(3);
        order.setUserId(2);
        order.setAddressId(1);
        order.setStatus("INSERT_TEST");
        orderMapper.insert(order);

        order = new Order();
        order.setOrderId(4);
        order.setUserId(2);
        order.setAddressId(1);
        order.setStatus("INSERT_TEST");
        orderMapper.insert(order);


        printData();
        System.out.println("-------------- insertTest Finish --------------");
    }

    @Override
    @Transactional
    public void processFailure() throws SQLException {
        System.out.println("-------------- Process Failure Begin ---------------");
        insertData();
        System.out.println("-------------- Process Failure Finish --------------");
        throw new RuntimeException("Exception occur for transaction test.");
    }

    private List<Long> insertData() throws SQLException {
        System.out.println("---------------------------- Insert Data ----------------------------");
        List<Long> result = new ArrayList<>(10);
        for (int i = 1; i <= 10; i++) {
            Order order = new Order();
            order.setUserId(i);
            order.setAddressId(i);
            order.setStatus("INSERT_TEST");
            orderMapper.insert(order);
            OrderItem item = new OrderItem();
            item.setOrderId(order.getOrderId());
            item.setUserId(i);
            item.setStatus("INSERT_TEST");
            orderItemMapper.insert(item);
            result.add(order.getOrderId());
        }
        return result;
    }

    private void deleteData(final List<Long> orderIds) throws SQLException {
        System.out.println("---------------------------- Delete Data ----------------------------");
        for (Long each : orderIds) {
            orderMapper.delete(each);
            orderItemMapper.delete(each);
        }
    }

    @Override
    public void printData() throws SQLException {
        System.out.println("---------------------------- Print Order Data -----------------------");
        for (Object each : orderMapper.selectAll()) {
            System.out.println(each);
        }
        System.out.println("---------------------------- Print OrderItem Data -------------------");
        for (Object each : orderItemMapper.selectAll()) {
            System.out.println(each);
        }
    }
}
