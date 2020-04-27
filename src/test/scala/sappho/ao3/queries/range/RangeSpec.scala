package sappho.ao3.queries.range

import org.scalatest.OneInstancePerTest
import org.scalatest.funspec.AnyFunSpec
import sappho.queries.range.{EmptyR, Exclusive, Inclusive, Infinite, Range}

class RangeSpec extends AnyFunSpec with OneInstancePerTest {
  describe("Infinite range of integers") {
    val range: Range[Int] = Range[Int](Infinite, Infinite)

    it("should contain any number") {
      assert(range contains 1)
    }
    it("should have the correct string representation") {
      assertResult("(-Inf;+Inf)")(range.toString)
    }
  }

  describe("The range (-Inf, 4]") {
    val range: Range[Int] = Range(Infinite, Inclusive(4))

    it("should contain 3") {
      assert(range contains 3)
    }
    it("should contain the bound 4") {
      assert(range contains 4)
    }
    it("should not contain 5") {
      assert(!(range contains 5))
    }
    it("should have the correct string representation") {
      assertResult("(-Inf;4]")(range.toString)
    }
  }

  describe("The range (-Inf, 4)") {
    val range: Range[Int] = Range(Infinite, Exclusive(4))

    it("should contain 3") {
      assert(range contains 3)
    }
    it("should not contain the bound 4") {
      assert(!(range contains 4))
    }
    it("should not contain 5") {
      assert(!(range contains 5))
    }
    it("should have the correct string representation") {
      assertResult("(-Inf;4)")(range.toString)
    }
  }

  describe("The range [4, +Inf)") {
    val range: Range[Int] = Range(Inclusive(4), Infinite)

    it("should not contain 3") {
      assert(!(range contains 3))
    }
    it("should contain the bound 4") {
      assert(range contains 4)
    }
    it("should contain 5") {
      assert(range contains 5)
    }
    it("should have the correct string representation") {
      assertResult("[4;+Inf)")(range.toString)
    }
  }

  describe("The range (4, +Inf)") {
    val range: Range[Int] = Range(Exclusive(4), Infinite)

    it("should not contain 3") {
      assert(!(range contains 3))
    }
    it("should not contain the bound 4") {
      assert(!(range contains 4))
    }
    it("should contain 5") {
      assert(range contains 5)
    }
    it("should have the correct string representation") {
      assertResult("(4;+Inf)")(range.toString)
    }
  }

  describe("The range (-7, 5)") {
    val range: Range[Int] = Range(Exclusive(-7), Exclusive(5))

    it("should not contain (-8)") {
      assert(!(range contains (-8)))
    }
    it("should not contain the bound (-7)") {
      assert(!(range contains (-7)))
    }
    it("should contain 5") {
      assert(range contains (-6))
    }
    it("should contain 4") {
      assert(range contains 4)
    }
    it("should not contain the bound 5") {
      assert(!(range contains 5))
    }
    it("should not contain 6") {
      assert(!(range contains 6))
    }
    it("should have the correct string representation") {
      assertResult("(-7;5)")(range.toString)
    }
  }

  describe("The range [2, 13)") {
    val range: Range[Int] = Range(Inclusive(2), Exclusive(13))

    it("should not contain 1") {
      assert(!(range contains 1))
    }
    it("should contain the bound 2") {
      assert(range contains 2)
    }
    it("should contain 3") {
      assert(range contains 3)
    }
    it("should contain 12") {
      assert(range contains 12)
    }
    it("should not contain the bound 13") {
      assert(!(range contains 13))
    }
    it("should not contain 14") {
      assert(!(range contains 14))
    }
    it("should have the correct string representation") {
      assertResult("[2;13)")(range.toString)
    }
  }

  describe("The range (-1, 5]") {
    val range: Range[Int] = Range(Exclusive(-1), Inclusive(5))

    it("should not contain (-2)") {
      assert(!(range contains (-2)))
    }
    it("should not contain the bound (-1)") {
      assert(!(range contains (-1)))
    }
    it("should contain 0") {
      assert(range contains 0)
    }
    it("should contain 4") {
      assert(range contains 4)
    }
    it("should contain the bound 5") {
      assert(range contains 5)
    }
    it("should not contain 6") {
      assert(!(range contains 6))
    }
    it("should have the correct string representation") {
      assertResult("(-1;5]")(range.toString)
    }
  }

  describe("The range [3; 20]") {
    val range: Range[Int] = Range(Inclusive(3), Inclusive(20))

    it("should not contain 2") {
      assert(!(range contains 2))
    }
    it("should contain the bound 3") {
      assert(range contains 3)
    }
    it("should contain 4") {
      assert(range contains 4)
    }
    it("should contain 19") {
      assert(range contains 19)
    }
    it("should contain the bound 20") {
      assert(range contains 20)
    }
    it("should not contain 21") {
      assert(!(range contains 21))
    }
    it("should have the correct string representation") {
      assertResult("[3;20]")(range.toString)
    }
  }

  describe("The singleton range [5;5]") {
    val range: Range[Int] = Range[Int](Inclusive(5), Inclusive(5))

    it("should exist") {
      assert(!range.isEmpty)
    }
    it("should not contain 4") {
      assert(!(range contains 4))
    }
    it("should contain its only element 5") {
      assert(range contains 5)
    }
    it("should not contain 6") {
      assert(!(range contains 6))
    }
    it("should have the correct string representation") {
      assertResult("[5;5]")(range.toString)
    }
  }
  describe("The empty range (3;3]") {
    it("should not exist") {
      assertResult(EmptyR[Int]())(Range(Exclusive(3), Inclusive(3)))
    }
  }
  describe("The empty range [3;3)") {
    it("should not exist") {
      assertResult(EmptyR[Int]())(Range(Inclusive(3), Exclusive(3)))
    }
  }
  describe("The empty range (3;3)") {
    it("should not exist") {
      assertResult(EmptyR[Int]())(Range(Exclusive(3), Exclusive(3)))
    }
  }

  describe("The invalid range (5;2)") {
    it("should not exist") {
      assertResult(EmptyR[Int]())(Range(Exclusive(5), Exclusive(2)))
    }
  }
  describe("The invalid range [5;2)") {
    it("should not exist") {
      assertResult(EmptyR[Int]())(Range(Inclusive(5), Exclusive(2)))
    }
  }
  describe("The invalid range (5;2]") {
    it("should not exist") {
      assertResult(EmptyR[Int]())(Range(Exclusive(5), Inclusive(2)))
    }
  }
  describe("The invalid range [5;2]") {
    it("should not exist") {
      assertResult(EmptyR[Int]())(Range(Inclusive(5), Inclusive(2)))
    }
  }

  describe("The unbounded range and itself") {
    val unbounded: Range[Int] = Range[Int](Infinite, Infinite)

    it("should have a union equal to the unbounded range") {
      assertResult(unbounded)(unbounded union unbounded)
    }
    it("should have an intersection equal to the finite range") {
      assertResult(unbounded)(unbounded intersect unbounded)
    }
  }
  describe("The unbounded range and the singleton range [5;5]") {
    val unbounded: Range[Int] = Range[Int](Infinite, Infinite)
    val singleton: Range[Int] = Range(Inclusive(5), Inclusive(5))

    it("should have a union equal to the unbounded range") {
      assertResult(unbounded)(unbounded union singleton)
    }
    it("should have an intersection equal to the singleton range") {
      assertResult(singleton)(unbounded intersect singleton)
    }
  }
  describe("The unbounded range and the finite range [2;3)") {
    val unbounded: Range[Int] = Range[Int](Infinite, Infinite)
    val finite: Range[Int] = Range(Inclusive(2), Exclusive(3))

    it("should have a union equal to the unbounded range") {
      assertResult(unbounded)(unbounded union finite)
    }
    it("should have an intersection equal to the finite range") {
      assertResult(finite)(unbounded intersect finite)
    }
  }
  describe("The unbounded range and the infinite range (-Inf;4]") {
    val unbounded: Range[Int] = Range[Int](Infinite, Infinite)
    val infinite: Range[Int] = Range(Infinite, Inclusive(4))

    it("should have a union equal to the unbounded range") {
      assertResult(unbounded)(unbounded union infinite)
    }
    it("should have an intersection equal to the infinite range") {
      assertResult(infinite)(unbounded intersect infinite)
    }
  }
  describe("The unbounded range and the infinite range (3;+Inf)") {
    val unbounded: Range[Int] = Range[Int](Infinite, Infinite)
    val infinite: Range[Int] = Range(Exclusive(3), Infinite)

    it("should have a union equal to the unbounded range") {
      assertResult(unbounded)(unbounded union infinite)
    }
    it("should have an intersection equal to the infinite range") {
      assertResult(infinite)(unbounded intersect infinite)
    }
  }
}
