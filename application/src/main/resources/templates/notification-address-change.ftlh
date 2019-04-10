<#ftl encoding="utf-8">
<p>Dear ${contact.academicTitle} ${contact.firstName} ${contact.lastName},</p>

<p>the company address of a business partner associated with you was changed.</p>

<p>Please use the following link to confirm changes or edit them when necessary:<br>
<a href="${confirmationLink}">${confirmationLink}</></p>

<table style="width: 40rem;">
<tbody>
<tr>
<td style="width: 8rem;">Person</td>
<td style="width: 32rem;">${address.person}</td>
</tr>
<tr>
<td style="width: 8rem;">Street</td>
<td style="width: 32rem;">${address.streetName}</td>
</tr>
<tr>
<td style="width: 8rem;">House Nr.</td>
<td style="width: 32rem;">${address.houseNumber}</td>
</tr>
<tr>
<td style="width: 8rem;">Supplement</td>
<td style="width: 32rem;">${address.houseNumberSupplementText}</td>
</tr>
<tr>
<td style="width: 8rem;">City code</td>
<td style="width: 32rem;">${address.cityCode}</td>
</tr>
<tr>
<td style="width: 8rem;">City</td>
<td style="width: 32rem;">${address.cityName}</td>
</tr>
<tr>
<td style="width: 8rem;">County</td>
<td style="width: 32rem;">${address.county}</td>
</tr>
<tr>
<td style="width: 8rem;">Country</td>
<td style="width: 32rem;">${address.country}</td>
</tr>
</tbody>
</table>
<p>Thank you very much in advance!</p>
<p>Best regards,</p>
<p>
	ACME Corp.<br/>
	Dietmar-Hopp-Allee 100<br/>
	64546 M&ouml;rfelden-Walldorf<br/>
	Germany
</p>
<p>
	Phone: +49 1234 567890 <br/>
	Fax:&nbsp;+49 1234 567899
</p>