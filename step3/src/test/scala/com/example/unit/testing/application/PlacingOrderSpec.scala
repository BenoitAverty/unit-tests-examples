package com.example.unit.testing.application

import java.time.LocalDate
import java.util.stream.Collectors

import com.example.unit.testing.domain.repositories.{CustomerRepository, OrderRepository}
import com.example.unit.testing.domain.{CreditCardService, Customer, Order}
import com.mmnaseri.utils.spring.data.dsl.factory.RepositoryFactoryBuilder._
import org.mockito.Mockito
import org.mockito.Matchers
import org.scalatest._
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.annotation.{Bean, ComponentScan, Configuration, Import}
import org.springframework.test.context.{ContextConfiguration, TestContextManager}

import scala.util.Random
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.junit.Test

@RunWith(classOf[JUnitRunner])
@ContextConfiguration(
  classes = Array(classOf[OrderApplicationSpecContext]),
  loader = classOf[SpringApplicationContextLoader])
class PlacingOrderSpec extends path.FunSpec with org.scalatest.Matchers {
  
  @Autowired val orderApplication: OrderApplication = null
  @Autowired val customerRepository: CustomerRepository = null
  @Autowired val orderRepository: OrderRepository = null
  @Autowired val creditCardService: CreditCardService = null

  new TestContextManager(this.getClass).prepareTestInstance(this)

  describe("Placing an Order") {

    describe("With an existing customer") {

      val customer = new Customer()
      customerRepository.save(customer)

      it("should return a valid order ID") {
        val orderId = orderApplication.placeOrder(123L, customer.getTid, "123")
        orderId should not be null
      }

      describe("When the credit card is not maxed out and not expired") {

        Mockito.when(creditCardService.isMaxedOut("123", 1.23)).thenReturn(false)
        Mockito.when(creditCardService.expirationDate("123")).thenReturn(LocalDate.MAX)

        it("Should create an order with status <IN PROGRESS>") {
          val orderId = orderApplication.placeOrder(123L, customer.getTid, "123")
          val orderStatus = orderApplication.getOrderStatus(orderId)

          orderStatus should be("IN PROGRESS")
        }

        it("Should create an order accessible from the customer") {
          val orderId = orderApplication.placeOrder(123L, customer.getTid, "123")
          val orders = orderApplication.findOrdersForCustomer(customer.getTid)

          orders should not be empty
          Inspectors.forExactly(1, orders) {
            _.getTid should be (orderId)
          }
        }

      }

      describe("When the credit card is maxed out") {

        Mockito.when(creditCardService.isMaxedOut("123", 1.23)).thenReturn(true)

        it("Should create an order with status <PAYMENT FAILED>") {
          val orderId = orderApplication.placeOrder(123L, customer.getTid, "123")
          val orderStatus = orderApplication.getOrderStatus(orderId)

          orderStatus should be("PAYMENT FAILED")
        }

        it("Should create an order not accessible from the customer") {
          val orderId = orderApplication.placeOrder(123L, customer.getTid, "123")
          val orders = orderApplication.findOrdersForCustomer(customer.getTid)

          Inspectors.forAll(orders) {
            _.getTid should not be(orderId)
          }
        }

      }

      describe("When the credit card is expired") {
        Mockito.when(creditCardService.isMaxedOut("123", 1.23)).thenReturn(false)
        Mockito.when(creditCardService.expirationDate("123")).thenReturn(LocalDate.now().plusMonths(1))

        it("Should create an order with status <PAYMENT FAILED>") {
          val orderId = orderApplication.placeOrder(123L, customer.getTid, "123")
          val orderStatus = orderApplication.getOrderStatus(orderId)

          orderStatus should be("PAYMENT FAILED")
        }

        it("Should create an order not accessible from the customer") {
          val orderId = orderApplication.placeOrder(123L, customer.getTid, "123")
          val orders = orderApplication.findOrdersForCustomer(customer.getTid)

          Inspectors.forAll(orders) {
            _.getTid should not be(orderId)
          }
        }
      }

      customerRepository.deleteAll()

    }

  }

}

@Configuration
@Import(Array(classOf[OrderApplication]))
@ComponentScan(Array("com.example.unit.testing.domain"))
class OrderApplicationSpecContext {
  @Bean def creditCardService: CreditCardService = {
    val mock = Mockito.mock(classOf[CreditCardService])

    // return nice value (this method not supposed to return null).
    Mockito.when(mock.expirationDate(Matchers.anyString())).thenReturn(LocalDate.MAX)

    return mock
  }

  @Bean def orderRepository: OrderRepository = {
    return builder.mock(classOf[OrderRepository])
  }

  @Bean def customerRepository: CustomerRepository = {
    return builder.mock(classOf[CustomerRepository])
  }
}