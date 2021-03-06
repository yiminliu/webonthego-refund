package com.trc.service;

import java.util.List;

import javax.xml.ws.WebServiceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trc.exception.service.PaymentServiceException;
import com.trc.service.gateway.WebserviceAdapter;
import com.trc.service.gateway.WebserviceGateway;
import com.trc.user.User;
import com.trc.web.session.SessionManager;
import com.tscp.mvne.Account;
import com.tscp.mvne.CreditCard;
import com.tscp.mvne.CustPmtMap;
import com.tscp.mvne.PaymentUnitResponse;
import com.tscp.mvne.TSCPMVNA;

@Service
public class PaymentService implements PaymentServiceModel {
	private TSCPMVNA port;

	@Autowired
	public void init(
			WebserviceGateway gateway) {
		this.port = gateway.getPort();
	}

	@Override
	public CreditCard getCreditCard(
			int paymentId) throws PaymentServiceException {
		try {
			CreditCard creditCard = port.getCreditCardDetail(paymentId);
			if (creditCard.getAddress2() != null && creditCard.getAddress2().equals("{}")) {
				creditCard.setAddress2(null);
			}
			return creditCard;
		} catch (WebServiceException e) {
			throw new PaymentServiceException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public CreditCard addCreditCard(
			User user,
			CreditCard creditCard) throws PaymentServiceException {
		try {
			if (user == null || creditCard.getIsDefault() == null) {
				creditCard.setIsDefault("N");
			}
			return port.addCreditCard(WebserviceAdapter.toCustomer(user), creditCard);
		} catch (WebServiceException e) {
			throw new PaymentServiceException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public List<CustPmtMap> removeCreditCard(
			User user,
			int paymentId) throws PaymentServiceException {
		try {
			List<CustPmtMap> paymentMapList = port.deleteCreditCardPaymentMethod(WebserviceAdapter.toCustomer(user), paymentId);
			if (!paymentMapList.isEmpty()) {
				boolean updateDefault = true;
				CustPmtMap newDefault = paymentMapList.get(0);
				for (CustPmtMap paymentMap : paymentMapList) {
					if (paymentMap.getIsDefault().equals("Y")) {
						updateDefault = false;
					}
				}
				if (updateDefault) {
					newDefault.setIsDefault("Y");
					return updatePaymentMap(newDefault);
				}
			}
			return paymentMapList;
		} catch (WebServiceException e) {
			throw new PaymentServiceException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public List<CustPmtMap> updateCreditCard(
			User user,
			CreditCard creditCard) throws PaymentServiceException {
		try {
			if (creditCard.getAddress2() == null || creditCard.getAddress2().isEmpty()) {
				creditCard.setAddress2("{}");
			}
			if (creditCard.getCreditCardNumber().toLowerCase().contains("x")) {
				creditCard.setCreditCardNumber(null);
			}
			List<CustPmtMap> paymentMapList = port.updateCreditCardPaymentMethod(WebserviceAdapter.toCustomer(user), creditCard);
			CustPmtMap paymentMap = getPaymentMap(paymentMapList, creditCard.getPaymentid());
			paymentMap.setPaymentalias(creditCard.getAlias());
			if (creditCard.getIsDefault() == null) {
				paymentMap.setIsDefault("N");
			} else {
				paymentMap.setIsDefault("Y");
			}
			return updatePaymentMap(paymentMap);
		} catch (WebServiceException e) {
			throw new PaymentServiceException(e.getMessage(), e.getCause());
		}
	}

	private CustPmtMap getPaymentMap(
			List<CustPmtMap> paymentMapList,
			int paymentId) {
		for (CustPmtMap custPmtMap : paymentMapList) {
			if (custPmtMap.getPaymentid() == paymentId)
				return custPmtMap;
		}
		return null;
	}

	@Override
	public List<CustPmtMap> getPaymentMap(
			User user) throws PaymentServiceException {
		try {
			return port.getCustPaymentList(user.getUserId(), 0);
		} catch (WebServiceException e) {
			throw new PaymentServiceException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public CustPmtMap getPaymentMap(
			User user,
			int paymentId) throws PaymentServiceException {
		try {
			List<CustPmtMap> result = port.getCustPaymentList(user.getUserId(), paymentId);
			return result.size() == 1 ? result.get(0) : null;
		} catch (WebServiceException e) {
			throw new PaymentServiceException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public List<CustPmtMap> updatePaymentMap(
			CustPmtMap custPmtMap) throws PaymentServiceException {
		try {
			return port.updateCustPaymentMap(custPmtMap);
		} catch (WebServiceException e) {
			throw new PaymentServiceException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public PaymentUnitResponse makePayment(
			User user,
			Account account,
			CreditCard creditCard,
			String amount) throws PaymentServiceException {
		try {
			return port.submitPaymentByCreditCard(SessionManager.getCurrentSession().getId(), account, creditCard, amount);
		} catch (WebServiceException e) {
			throw new PaymentServiceException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public PaymentUnitResponse makePayment(
			User user,
			Account account,
			int paymentId,
			String amount) throws PaymentServiceException {
		try {
			return port.submitPaymentByPaymentId(SessionManager.getCurrentSession().getId(), WebserviceAdapter.toCustomer(user), paymentId, account, amount);
		} catch (WebServiceException e) {
			throw new PaymentServiceException(e.getMessage(), e.getCause());
		}
	}

}
