localhost:8085/rest/personController/getAll

{
	getAllPersons{
		id
		firstName
		lastName
		mobile
		email
		address
	}
}


localhost:8085/rest/personController/findPersonByFirstName

{
	findPersonByFirstName(firstName:"Sahan"){
		id
		firstName
		lastName
		mobile
		email
		address
	}
}