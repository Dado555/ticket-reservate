package support;

import java.util.List;
import java.util.stream.Collectors;

import model.Salesman;
import model.enums.UserType;
import web.dto.SalesmanDTO;


public class SalesmanToSalesmanDTO implements Converter<Salesman, SalesmanDTO>{

	@Override
	public SalesmanDTO convert(Salesman salesman) {
		return new SalesmanDTO(salesman.getUsername(),
							   salesman.getFirstName(),
							   salesman.getLastName(),
							   salesman.getGender(),
							   salesman.getBirthDate(),
							   UserType.SALESMAN,
							   salesman.getManifestationIDs(),
							   salesman.isDeleted());
	}

	public List<SalesmanDTO> convert(List<Salesman> salesmans) {
		return salesmans.stream().map(this::convert).collect(Collectors.toList());
	}
}
