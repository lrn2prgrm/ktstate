package com.corlaez.ktstate.nested

import com.corlaez.ktstate.InvalidInitialStateException
import com.corlaez.ktstate.InvalidTransitionException
import com.corlaez.ktstate.UnreachableStateException
import com.corlaez.ktstate.simple.Event
import com.corlaez.ktstate.simple.EventTarget
import com.corlaez.ktstate.simple.SimpleState
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldThrow
import io.kotlintest.specs.StringSpec
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class NestedSimpleMachineTest : StringSpec() {

    init {
        simpleTests()
        nestedTests()
    }

    private fun simpleTests() {
        "Should check initialState" {
            shouldThrow<InvalidInitialStateException> {
                NestedSimpleMachine(
                        SimpleState("A"),
                        mapOf()
                )
            }
        }
        "Should check transitions" {
            shouldThrow<InvalidTransitionException> {
                NestedSimpleMachine(
                        SimpleState("A"),
                        mapOf(
                                SimpleState("A") to NestedStateDef(
                                        mapOf(Event("x") to EventTarget("B"))
                                ),
                                SimpleState("B") to NestedStateDef(
                                        mapOf(Event("y") to EventTarget("C"))
                                )
                        )
                )
            }
        }
        "Should check transitions on InitialState" {
            shouldThrow<InvalidTransitionException> {
                NestedSimpleMachine(
                        SimpleState("A"),
                        mapOf(
                                SimpleState("A") to NestedStateDef(
                                        mapOf(Event("x") to EventTarget("C"))
                                )
                        )
                )
            }
        }
        "Should check for unreachable states" {
            shouldThrow<UnreachableStateException> {
                NestedSimpleMachine(
                        SimpleState("A"),
                        mapOf(
                                SimpleState("A") to NestedStateDef(
                                        mapOf(Event("x") to EventTarget("A"))
                                ),
                                SimpleState("C") to NestedStateDef(
                                        mapOf(Event("y") to EventTarget("A"))
                                )
                        )
                )
            }
        }
        "Should check for unreachable states that target themselves" {
            shouldThrow<UnreachableStateException> {
                NestedSimpleMachine(
                        SimpleState("A"),
                        mapOf(
                                SimpleState("A") to NestedStateDef(
                                        mapOf(Event("x") to EventTarget("A"))
                                ),
                                SimpleState("C") to NestedStateDef(
                                        mapOf(Event("y") to EventTarget("C"))
                                )
                        )
                )
            }
        }
        "Should allow empty eventTargets" {
            NestedSimpleMachine(
                    SimpleState("A"),
                    mapOf(
                            SimpleState("A") to NestedStateDef(
                                    mapOf(Event("x") to EventTarget(null))
                            )
                    )
            )
        }

        "Should allow progressing state" {
            val machine = NestedSimpleMachine(
                    SimpleState("A"),
                    mapOf(
                            SimpleState("A") to NestedStateDef(
                                    mapOf(Event("x") to EventTarget("B"))
                            ),
                            SimpleState("B") to NestedStateDef()
                    )
            )
            val initial = machine.initialState
            machine.nextState(initial, Event("x")) shouldBe SimpleState("B")
        }
        "Should return the same state when ignoring events" {
            val machine = NestedSimpleMachine(
                    SimpleState("A"),
                    mapOf(
                            SimpleState("A") to NestedStateDef(
                                    mapOf(Event("x") to EventTarget("B"))
                            ),
                            SimpleState("B") to NestedStateDef()
                    )
            )
            val initial = machine.initialState
            machine.nextState(initial, Event("y")) shouldBe SimpleState("A")
        }
    }

    private fun nestedTests() {

    }
}
