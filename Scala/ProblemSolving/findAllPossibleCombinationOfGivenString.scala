object FindPermutations {

  def nextCycle(strings: List[String]): List[String] = {
    strings match {
      case Nil          => Nil
      case head :: tail => tail :+ head
    }
  }

  def cycles(strings: List[String]): List[List[String]] = {
    def loop(l: List[String], acc: List[List[String]]): List[List[String]] = {
      if (acc.length == l.length) acc
      else {
        val next = nextCycle(l)
        loop(next, next :: acc)
      }
    }
    loop(strings, List(strings))
  }

  def findPermutations(l: List[String]): List[List[String]] = {
    l match {
      case Nil         => println("Empty List"); List(List())
      case head :: Nil => println("Only One Element"); List(List(head))
      case first :: second :: Nil =>
        List(List(second, first), List(first, second))
      case _ => {
        val cycledList = cycles(l)
        cycledList.flatMap { cycle =>
          val h = cycle.head
          val t = cycle.tail
          val permutedList = findPermutations(t)
          permutedList.map(pList => h :: pList)
        }
      }
    }
  }

  def main(args: Array[String]): Unit = {

    findPermutations("abc".split("").toList).foreach(x =>
      println(x.mkString("")))

  }

}
