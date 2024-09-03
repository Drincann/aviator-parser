import { describe, it } from "node:test"
import assert from 'assert/strict'
import { AviatorExpressionParser } from "../src/parser.js"

describe("parser", () => {
  it("logic or", () => {
    const exp = `deviceId == 'EA2BE91A-2790-45B5-BB1C-229B6AC09366' || deviceId == '24680522-11B0-4ED3-8CEE-5288D5701E88' || deviceId == '401D60F4-94CC-491C-AC03-7EFB0F7FA80B' || deviceId == '8FB189B3-E38D-4130-98CF-703AA24629D4' || deviceId == 'BAEC7EBA-1E07-4A29-AC02-7EBAC61998BA' || deviceId == '74056989-DC8E-43BF-B96E-CEBFD6BD5D47'`
    const parser = new AviatorExpressionParser(exp)
    const root = parser.parse()
    assertAssignable(root, {
      type: 'binary-expression',
      left: {
        type: 'binary-expression',
        left: {
          type: 'binary-expression',
          left: {
            type: 'binary-expression',
            left: {
              type: 'binary-expression',
              left: {
                type: 'binary-expression',
                left: { type: 'identifier', name: 'deviceId' },
                operator: 'Equal',
                right: {
                  type: 'string-literal',
                  value: 'EA2BE91A-2790-45B5-BB1C-229B6AC09366'
                }
              },
              operator: 'LogicOr',
              right: {
                type: 'binary-expression',
                left: { type: 'identifier', name: 'deviceId' },
                operator: 'Equal',
                right: {
                  type: 'string-literal',
                  value: '24680522-11B0-4ED3-8CEE-5288D5701E88'
                }
              }
            },
            operator: 'LogicOr',
            right: {
              type: 'binary-expression',
              left: { type: 'identifier', name: 'deviceId' },
              operator: 'Equal',
              right: {
                type: 'string-literal',
                value: '401D60F4-94CC-491C-AC03-7EFB0F7FA80B'
              }
            }
          },
          operator: 'LogicOr',
          right: {
            type: 'binary-expression',
            left: { type: 'identifier', name: 'deviceId' },
            operator: 'Equal',
            right: {
              type: 'string-literal',
              value: '8FB189B3-E38D-4130-98CF-703AA24629D4'
            }
          }
        },
        operator: 'LogicOr',
        right: {
          type: 'binary-expression',
          left: { type: 'identifier', name: 'deviceId' },
          operator: 'Equal',
          right: {
            type: 'string-literal',
            value: 'BAEC7EBA-1E07-4A29-AC02-7EBAC61998BA'
          }
        }
      },
      operator: 'LogicOr',
      right: {
        type: 'binary-expression',
        left: { type: 'identifier', name: 'deviceId' },
        operator: 'Equal',
        right: {
          type: 'string-literal',
          value: '74056989-DC8E-43BF-B96E-CEBFD6BD5D47'
        }
      }
    })
  })

  it("logic and with comment", () => {
    const exp = `R_IAPRiskUserHigh==1&&business!='WEB'&&platform=='ios'## && channel=='APPLE_IAP' ## && business=='CASH_WALLET'`
    const parser = new AviatorExpressionParser(exp)
    const root = parser.parse()
    assertAssignable(root, {
      type: 'binary-expression',
      left: {
        type: 'binary-expression',
        left: {
          type: 'binary-expression',
          left: { type: 'identifier', name: 'R_IAPRiskUserHigh' },
          operator: 'Equal',
          right: { type: 'number-literal', value: 1 }
        },
        operator: 'LogicAnd',
        right: {
          type: 'binary-expression',
          left: { type: 'identifier', name: 'business' },
          operator: 'NotEqual',
          right: { type: 'string-literal', value: 'WEB' }
        }
      },
      operator: 'LogicAnd',
      right: {
        type: 'binary-expression',
        left: { type: 'identifier', name: 'platform' },
        operator: 'Equal',
        right: { type: 'string-literal', value: 'ios' }
      }
    })
  })

  it("logic and & or", () => {
    const exp = `R_IAPRiskUserHigh==1&&business!='WEB'&&platform=='ios'||channel=='APPLE_IAP'&&business=='CASH_WALLET'`
    const parser = new AviatorExpressionParser(exp)
    const root = parser.parse()
    assertAssignable(root, {
      type: 'binary-expression',
      left: {
        type: 'binary-expression',
        left: {
          type: 'binary-expression',
          left: {
            type: 'binary-expression',
            left: { type: 'identifier', name: 'R_IAPRiskUserHigh' },
            operator: 'Equal',
            right: { type: 'number-literal', value: 1 }
          },
          operator: 'LogicAnd',
          right: {
            type: 'binary-expression',
            left: { type: 'identifier', name: 'business' },
            operator: 'NotEqual',
            right: { type: 'string-literal', value: 'WEB' }
          }
        },
        operator: 'LogicAnd',
        right: {
          type: 'binary-expression',
          left: { type: 'identifier', name: 'platform' },
          operator: 'Equal',
          right: { type: 'string-literal', value: 'ios' }
        }
      },
      operator: 'LogicOr',
      right: {
        type: 'binary-expression',
        left: {
          type: 'binary-expression',
          left: { type: 'identifier', name: 'channel' },
          operator: 'Equal',
          right: { type: 'string-literal', value: 'APPLE_IAP' }
        },
        operator: 'LogicAnd',
        right: {
          type: 'binary-expression',
          left: { type: 'identifier', name: 'business' },
          operator: 'Equal',
          right: { type: 'string-literal', value: 'CASH_WALLET' }
        }
      }
    })
  })

  it("binary expression & unary expression", () => {
    const exp = `R_IAPRiskUserHigh==1&&!isXxx!=true`
    const parser = new AviatorExpressionParser(exp)
    const root = parser.parse()
    assertAssignable(root, {
      type: 'binary-expression',
      left: {
        type: 'binary-expression',
        left: { type: 'identifier', name: 'R_IAPRiskUserHigh' },
        operator: 'Equal',
        right: { type: 'number-literal', value: 1 }
      },
      operator: 'LogicAnd',
      right: {
        type: 'binary-expression',
        left: { type: 'unary-expression', operator: 'LogicNot', argument: { type: 'identifier', name: 'isXxx' } },
        operator: 'NotEqual',
        right: { type: 'boolean-literal', value: true }
      }
    })
  })

  it("regex", () => {
    const exp = `(isVirtualPhone4Shield == 1 || phoneNumberNew =~ /86192.*/) && build < 7330000 && isUserRecentRegister72hour == 1`
    const parser = new AviatorExpressionParser(exp)
    const root = parser.parse()
    assertAssignable(root, {
      type: 'binary-expression',
      left: {
        type: 'binary-expression',
        left: {
          type: 'binary-expression',
          left: {
            type: 'binary-expression',
            left: { type: 'identifier', name: 'isVirtualPhone4Shield' },
            operator: 'Equal',
            right: { type: 'number-literal', value: 1 }
          },
          operator: 'LogicOr',
          right: {
            type: 'binary-expression',
            left: { type: 'identifier', name: 'phoneNumberNew' },
            operator: 'Like',
            right: { type: 'regex-literal', value: '86192.*' }
          }
        },
        operator: 'LogicAnd',
        right: {
          type: 'binary-expression',
          left: { type: 'identifier', name: 'build' },
          operator: 'LessThan',
          right: { type: 'number-literal', value: 7330000 }
        }
      },
      operator: 'LogicAnd',
      right: {
        type: 'binary-expression',
        left: { type: 'identifier', name: 'isUserRecentRegister72hour' },
        operator: 'Equal',
        right: { type: 'number-literal', value: 1 }
      },
    })
  })

  it("function call", () => {
    const exp = `contains(tinyAccessibilityServices,"com.wtkj.app.clicker")`
    const parser = new AviatorExpressionParser(exp)
    const root = parser.parse()
    assertAssignable(root, {
      type: 'function-call',
      name: 'contains',
      arguments: [
        { type: 'identifier', name: 'tinyAccessibilityServices' },
        { type: 'string-literal', value: 'com.wtkj.app.clicker' }
      ]
    })
  })
})

function assertAssignable(obj: Record<string, any>, isAssignableTo: Record<string, any>, path = '$') {
  for (const key in isAssignableTo) {
    if (typeof isAssignableTo[key] === 'object') {
      assertAssignable(obj[key], isAssignableTo[key], `${path}.${key}`)
      continue
    }
    if (typeof isAssignableTo[key] === 'undefined') {
      assert.equal(obj[key], isAssignableTo[key], `key: ${path}.${key}, excepted <${isAssignableTo[key]}>, got <${obj[key]}>`)
    }
    if (typeof isAssignableTo[key] === 'function') {
      continue
    }
    if (Array.isArray(isAssignableTo[key])) {
      assert.equal(obj[key].length, isAssignableTo[key].length)
      for (let i = 0; i < isAssignableTo[key].length; i++) {
        assertAssignable(obj[key][i], isAssignableTo[key][i], `${path}.${key}[${i}]`)
      }
      continue
    }

    assert.equal(obj[key], isAssignableTo[key], `key: ${path}.${key}, excepted <${isAssignableTo[key]}>, got ${obj[key]}>`)
  }
}