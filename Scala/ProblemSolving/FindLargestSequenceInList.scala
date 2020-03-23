Find largest sequence in a given list of numbers ip: [1,2,3,2,4,5,6,7,8,1,0,4,5,6] op:[4,5,6,7,8]

object FindLargestSequenceInList {

  def getLargestSequence(inputList: List[Int]): List[Int] = {
    def inner(inputList: List[Int],
              ls: List[Int],
              pls: List[Int]): List[Int] = {
      if (inputList.isEmpty) pls
      else {
        val element = inputList.head
        val nextElement =
          if (ls.isEmpty || element == ls.head + 1) element +: ls
          else List()
        if(nextElement.isEmpty)
           inner(inputList.tail, nextElement, if(pls.size > ls.size) pls else ls )
        else
           inner(inputList.tail,nextElement, if(pls.size > nextElement.size) pls else nextElement)
      }
    }
    inner(inputList,List(),List()).reverse
  }

  def main(args: Array[String]): Unit = {}
   println(getLargestSequence(List(1,2,3,2,4,5,6,7,8,1,0,4,5,6)))
}
