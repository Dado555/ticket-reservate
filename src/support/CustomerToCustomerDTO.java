package support;

import java.util.List;
import java.util.stream.Collectors;

import model.Customer;
import model.enums.UserType;
import web.dto.CustomerDTO;

public class CustomerToCustomerDTO implements Converter<Customer, CustomerDTO> {


	@Override
	public CustomerDTO convert(Customer customer) {
		return new CustomerDTO(customer.getUsername(),
							   customer.getFirstName(),
							   customer.getLastName(),
							   customer.getGender(),
							   customer.getBirthDate(),
							   UserType.CUSTOMER,
							   customer.getTicketIDs(),
							   customer.getCustomerType(),
							   customer.getPoints(),
							   customer.getCart(),
							   customer.isDeleted(),
							   customer.isBlocked(),
							   customer.isSuspicious());
	}
	
	public List<CustomerDTO> convert(List<Customer> customers) {
		return customers.stream().map(this::convert).collect(Collectors.toList());
	}

}
