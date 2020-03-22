/*

http://sqlandhadoop.com/how-to-implement-recursive-queries-in-spark/

EmployeeID	EmployeeName	ManagerID
2	             A	          4
4	             B	          6
5	             H	          7
6	             C	          5
7              F	
8	             D	          9
9	             E	

find Org Hirerchy for given employeeid

*/


def rec(df: DataFrame, id: Int): DataFrame = {
  val df4 = df.filter($"EmployeeID" === id)
  def inner(df1: DataFrame, df2: DataFrame): DataFrame = {
    val currRow = df1.take(1)
    if (currRow.length == 0) {
      df2
    } else {
      val df3 = df.filter($"EmployeeID" === currRow(0)(2))
      inner(df3, df2.unionByName(df3))
    }
  }
  if (id == 0) {
    df4
  } else {
    inner(df4, df4)
  }
}
