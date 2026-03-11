@TypeRegistration(basicClass = Cep.class, userType = CepUserType.class)
@TypeRegistration(basicClass = Cnpj.class, userType = CnpjUserType.class)
@TypeRegistration(basicClass = Cpf.class, userType = CpfUserType.class)
@TypeRegistration(basicClass = InscricaoEstadual.class, userType = InscricaoEstadualUserType.class)
@TypeRegistration(basicClass = Money.class, userType = MoneyUserType.class)
@TypeRegistration(basicClass = PhoneBrazil.class, userType = PhoneBrazilUserType.class)
@TypeRegistration(basicClass = Phone.class, userType = PhoneUserType.class)
@TypeRegistration(basicClass = SimpleTime.class, userType = SimpleTimeUserType.class)
package org.nextframework.types.hibernate;

import org.hibernate.annotations.TypeRegistration;
import org.nextframework.types.Cep;
import org.nextframework.types.Cnpj;
import org.nextframework.types.Cpf;
import org.nextframework.types.InscricaoEstadual;
import org.nextframework.types.Money;
import org.nextframework.types.Phone;
import org.nextframework.types.PhoneBrazil;
import org.nextframework.types.SimpleTime;
